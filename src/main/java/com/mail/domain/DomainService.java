package com.mail.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DomainService {
	@Autowired
	Domaindao dd;
	@Autowired
	Domainvo dv;
	


}
