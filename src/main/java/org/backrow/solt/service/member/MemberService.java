package org.backrow.solt.service.member;

import org.backrow.solt.domain.member.Member;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.dto.file.UploadResultDTO;

public interface MemberService {

    MemberInfoDTO getMemberInfo(long memberId);

    void modifyMember(ModifyDTO modifyDTO);

    void deleteMember(long memberId);

    void modifyMemberImage(long memberId, UploadResultDTO uploadResultDTO);

    void deleteMemberImage(long memberId);
}
