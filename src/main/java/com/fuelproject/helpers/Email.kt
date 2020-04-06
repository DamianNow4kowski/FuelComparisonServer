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
    private var mailSession: Session? = null
    private var props: Properties = Properties()

    init {
        props["mail.transport.protocol"] = "smtps"
        props["mail.smtps.auth"] = "true"
        // Zainicjowanie sesji
        mailSession = Session.getDefaultInstance(props)
    }

    fun sendEmail(to: String?): Boolean {
        // Tworzenie wiadomości email
        val message = MimeMessage(mailSession)
        return try {
            message.subject = messageSubject
            message.setContent(messageContent, "text/plain; charset=ISO-8859-2")
            message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            val transport: Transport = mailSession!!.transport
            transport.connect(host, port, from, password)
            // wysłanie wiadomości
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
            transport.close()
            true
        } catch (e: MessagingException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val host = "smtp.gmail.com"
        private const val port = 465
    }
}
