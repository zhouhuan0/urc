package com.yks.urc.seq.bp.impl;

import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;

import com.yks.distributed.cache.core.AtomicSequenceGenerator;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.seq.bp.api.ISeqBp;

@Component
public class SeqBpImpl implements ISeqBp {

	public long getNextSeq(String strSeqName) {
		String seqName = strSeqName;
		String strSeq = AtomicSequenceGenerator.generateSequenceStr(seqName);
		System.out.println(strSeq);
		String lSeq = strSeq.substring(seqName.length() + 10);
		long lNow = System.currentTimeMillis();
		long uuid = Long.parseLong(String.format("%s%s", lNow, lSeq));
		return uuid;
	}

	public long getNextRoleId() {
		return getNextSeq("roleId");
	}

	@Override
	public long getNextDataRuleId() {
		return getNextSeq("dataRuleId");
	}

	@Override
	public long getNextDataRuleTemplId() {
		return getNextSeq("dataRuleTemplId");
	}

	@Override
	public long getNextDataRuleSysId() {
		return getNextSeq("dataRuleSysId");
	}

	@Override
	public long getNextSqlId() {
		return getNextSeq("sqlId");
	}

	@Override
	public long getNextAuthWayId() {
		return getNextSeq("authWayId");

	}

	@Override
	public long getNextDataRuleObjId() {
		return getNextSeq("dataRuleObjId");
	}
}
