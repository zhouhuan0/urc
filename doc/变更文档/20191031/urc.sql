permit_item_user
permit_item_info
permit_refresh_task
yks_task
yks_prop_setting


key:redisson_lock_config
value:

dev:
{
	"serverType": "redis-single",
	"address": "redis://10.104.19.4:6379",
	"database": 0,
	"clusterAddress": ["redis://10.90.1.248:7001",
	"redis://10.90.1.249:7003"]
}

test:
{
	"serverType": "redis-single",
	"address": "redis://10.97.203.94:6379",
	"database": 1,
	"clusterAddress": ["redis://10.90.1.248:7001",
	"redis://10.90.1.249:7003"]
}

