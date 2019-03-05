package com.mail.domain;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Domainvo {
	private String address; //서버확인용 도메인주소
	private String stat; //서버 생존 유무
	private int master_seq; //도메인 주인의 회원번호
	private String reg_date;//등록시간
}
