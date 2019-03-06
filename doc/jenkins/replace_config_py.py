import requests
import os
import re
import string
import sys
import random
import json
from threading import Thread

# from concurrent.futures import ThreadPoolExecutor

# 获取opsmind接口token的用户名及密码
API_USER = 'opsuser'
API_PASSWORD = 'yks147258369'

# jenkins的根路径
JENKINS_BASE_DIR = '/var/lib/jenkins'


class ReplaceThread(Thread):
    def __init__(self, file, conf_map):
        super(ReplaceThread, self).__init__()
        self.file = file
        self.conf_map = conf_map

        self.exitcode = 0
        self.exception = None

    def run(self):
        try:
            self._run()
        except Exception as e:
            self.exitcode = 1
            self.exception = e

    def _run(self):
        try:
            with open(self.file, 'r') as f:
                content = f.read()
        except Exception as e:
            raise Exception('加载配置文件[%s]出错: %s' % (self.file, str(e)))

        # 需替换的变量的样式为: $[PASSWORD]$
        wrapper_pattern = re.compile('\$\[([\w-]+)\]\$')
        var_list = wrapper_pattern.findall(content)

        for var in var_list:
            try:
                value = self.conf_map[var]
                wrapper = "$[%s]$" % var
                content = content.replace(wrapper, value)
            except KeyError:
                raise Exception('[%s]文件中存在未知的key: %s,请先在opsmind-项目配置中心中添加此key' % (self.file, var))

        with open(self.file, 'w') as f:
            f.write(content)


class ConfigReplace(object):
    def __init__(self, job, job2):
        """
        :param job: jenkins job 名称
        conf_files: 包含的配置文件列表
        """
        self.job = job
        self.job2 = job2

        self.conf_files = []

    def get_config_info(self):
        """
        通过opsmind接口获取指定项目的配置项，以及配置文件路径
        :return: {
            'conf_path': 'source/conf/',
            'conf_map': {
                'DB_HOST': 'db.x.com',
                '_ZK_': 'x.x.x.x:2181,x.x.x.x:2181,x.x.x.x:2181',
                'DB_PASSWD': '12345',
            }
        }
        """

        try:
            # 第1步: 获取token
            token_url = 'https://opsmind.youkeshu.com/api/1.0/account/token'
            # token_url = 'http://127.0.0.1:8080/api/1.0/account/token'
            account = {"username": API_USER, "password": API_PASSWORD}
            headers = {"Content-Type": "application/json"}
            res = requests.post(url=token_url, headers=headers, data=json.dumps(account)).json()
            token = res.get('token')

            config_all = {
                'conf_path': "",
                'conf_map': {}
            }

            # 第2步: 获取配置信息
            headers['Authorization'] = "JWT {}".format(token)
            conf_api_url = 'https://opsmind.youkeshu.com/api/1.0/project/config/all?jenkins_job=%s' % self.job
            # conf_api_url = 'http://127.0.0.1:8080/api/1.0/project/config/all?jenkins_job=%s' % self.job
            res = requests.get(headers=headers, url=conf_api_url)
            info = res.json()

            if not isinstance(info, list):
                # 正确的返回消息格式为list, 失败的返回消息格式: {'status': False, 'message': '您无权限查看'}
                message = info.get('message', '获取项目配置信息失败')
                raise Exception(message)
            if not info:
                raise Exception('配置中心内获取到此jenkins对应的环境的配置信息为空')

            conf_map = {}
            for i in info:
                if i['key'] == "config_path":
                    if i['value'].startswith('/'):
                        raise Exception('配置文件路径请填写相对路径,勿填写绝对路径')

                    config_all['conf_path'] = i['value']

                conf_map[i['key']] = i['value']

            if 'config_path' not in conf_map.keys():
                raise Exception('此项目未设置配置文件路径,请先去配置中心添加key: "config_path" ')

            config_all['conf_map'] = conf_map

            return config_all

        except Exception:
            raise Exception('获取配置中心信息失败')

    def list_file(self, root):
        # 取出指定路径下的所有文件，包含递归子目录里的文件
        items = os.listdir(root)
        for i in items:
            i_path = os.path.join(root, i)
            if os.path.isdir(i_path):
                self.list_file(i_path)
            else:
                self.conf_files.append(i_path)

    # @staticmethod
    # def replace_config(file, conf_map, tmp_conf_path):
    #     with open(file, 'r') as f:
    #         content = f.read()
    #
    #     # 需替换的变量的样式为: $[PASSWORD]$
    #     wrapper_pattern = re.compile('\$\[([\w-]+)\]\$')
    #     var_list = wrapper_pattern.findall(content)
    #
    #     for var in var_list:
    #         try:
    #             value = conf_map[var]
    #             wrapper = "$[%s]$" % var
    #             content = content.replace(wrapper, value)
    #         except KeyError:
    #             print('key error')
    #             os.system("mv {} /tmp".format(tmp_conf_path))
    #             raise Exception('[%s]文件中存在未知的key: %s,请先在opsmind-配置中心中添加此key' % (file, var))
    #
    #     with open(file, 'w') as f:
    #         f.write(content)

    def main(self):
        # 第1步: 从opsmind接口获取到项目的配置项kv,以及配置文件路径
        info = self.get_config_info()

        # 1-1:获取key/value变量组成的字典
        conf_map = info.get('conf_map')

        # 1-2:去除配置文件相对路径的前后'/',避免产生影响
        conf_path = info.get('conf_path').strip('/')

        jenkins_base_dir = os.path.dirname(os.path.abspath(__file__))  # /var/lib/jenkins

        # 第2步: 创建临时配置文件目录，将原配置文件路径下的文件复制到临时路径下
        #if jenkins_base_dir != JENKINS_BASE_DIR:
        #    raise Exception('错误的运行路径,请放置在jenkins根路径运行')

        #base_conf_path = os.path.join(jenkins_base_dir, "jobs", self.job2, "workspace", conf_path)
        base_conf_path = os.path.join(self.job2, conf_path)
        # 创建一个临时配置目录，将配置路径内的文件都copy到这个临时目录来，在临时目录进行修改，修改完成后再替换掉原配置文件
        # 取一个10位随机数
        tmp_str = "".join([random.choice(string.ascii_letters) if random.randint(0, 1) else
                           random.choice(string.digits) for _ in range(10)])

        tmp_conf_path = base_conf_path + '_' + tmp_str
        if os.system("mkdir -p {} && cp -r {}/* {}/".format(tmp_conf_path, base_conf_path, tmp_conf_path)) != 0:
            raise Exception('创建临时配置文件目录失败')

        # 第3步: 获取到所有的配置文件，使用多线程开始替换
        self.list_file(tmp_conf_path)
        if not self.conf_files:
            os.system("mv {} /tmp".format(tmp_conf_path))
            raise Exception('未找到配置文件,请检查配置中心内的config_path变量是否正确填写')

            # 主线程无法直接捕获子线程内的异常，因此自定义了Thread类，在子线程内定义其出现异常时的返回值，在主线程内根据返回值
            # 来判断是否出现异常，并进行下一步操作

            # thpool = ThreadPoolExecutor(5)
            # for file in self.conf_files:
            #     thpool.submit(self.replace_config, file, conf_map)
            # thpool.shutdown(wait=True)
            # for file in self.conf_files:
            # self.replace_config(file, conf_map, tmp_conf_path)

        t_objs = []
        for file in self.conf_files:
            t = ReplaceThread(file=file, conf_map=conf_map)
            t.start()
            t_objs.append(t)

        for t in t_objs:
            t.join()
            if t.exitcode != 0:
                os.system("mv {} /tmp".format(tmp_conf_path))
                raise Exception(t.exception)

        # 第4步: kv替换完成后，临时配置文件覆盖掉原来的配置文件.文件操作尽量避免使用rm命令,避免误删.使用mv移动到tmp下
        cmd = "rm -rf {}/* && mv {}/* {}/ && mv {} /tmp".format(base_conf_path, tmp_conf_path,
                                                                base_conf_path, tmp_conf_path)

        if os.system(cmd) != 0:
            os.system("mv {} /tmp".format(tmp_conf_path))
            raise Exception('临时配置文件覆盖原配置文件时出现异常')


if __name__ == '__main__':
    if len(sys.argv) < 3:
        raise Exception('参数不足')
    job = sys.argv[1]
    job2 = sys.argv[2]
    foo = ConfigReplace(job=job, job2=job2)
    foo.main()
