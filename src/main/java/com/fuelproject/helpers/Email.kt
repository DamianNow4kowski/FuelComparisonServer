package com.fuelproject.helpers

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


abstract class Email internal constructor() {
    var from: String? = null
    var password: String? = null
    var messageSubject: String? = null
    var messageContent: String? = null

    fun sendEmail(to: String?) {
        val props = Properties()
        props["mail.transport.protocol"] = "smtps"
        props["mail.smtps.auth"] = "true"
        // Zainicjowanie sesji
        val mailSession: Session = Session.getDefaultInstance(props)
        // mailSession.setDebug(true);

        // Tworzenie wiadomości email
        val message = MimeMessage(mailSession)
        try {
            message.subject = messageSubject
            message.setContent(messageContent, "text/plain; charset=ISO-8859-2")
            message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            val transport: Transport = mailSession.getTransport()
            transport.connect(host, port, from, password)
            // wysłanie wiadomości
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
            transport.close()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val host = "smtp.gmail.com"
        private const val port = 465
    }
}
