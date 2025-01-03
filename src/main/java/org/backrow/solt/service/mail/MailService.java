package org.backrow.solt.service.mail;

import org.springframework.mail.SimpleMailMessage;

public interface MailService {

    void sendEmail(String toEmail, String title, String text);

    SimpleMailMessage createEmailForm(String toEmail, String title, String text);
}
