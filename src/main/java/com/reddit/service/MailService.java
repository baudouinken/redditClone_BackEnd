package com.reddit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.reddit.exception.SpringRedditException;
import com.reddit.model.NotificationEmail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private MailContentBuilder mailContentBuilder;

  @Async
  void sendMessage(NotificationEmail notificationEmail) {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom("keunne.baudoin@yahoo.fr");
      messageHelper.setTo(notificationEmail.getRecipient());
      messageHelper.setSubject(notificationEmail.getSubject());
      messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
    };

    try {
      mailSender.send(messagePreparator);
      log.info("Activation Mail Sent !");
    } catch (MailException e) {
      log.error("Exception occurred when sending mail", e);
      throw new SpringRedditException("Exception occurred when sending mail to " + notificationEmail.getRecipient(), e);
    }
  }

}
