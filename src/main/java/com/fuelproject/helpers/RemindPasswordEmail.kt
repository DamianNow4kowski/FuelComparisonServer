package com.fuelproject.helpers

class RemindPasswordEmail(newPassword: String) : Email() {
    init {
        from = "apk.app@gmail.com"
        password = "password"
        messageSubject = "Porównywarka cen paliw- przypomnienie hasła"
        messageContent = """
            Szanowny użytkowniku,
            Wiadomość została wygenerowana automatycznie przez system.
            
            Twoje hasło to: $newPassword
            Dla bezpieczeństwa zalecamy zmianę hasła po zalogowaniu.
            
            Pozdrawiamy,
            Zespół aplikacji!
            
            Jeśli nie używałeś mechanizmu przypominania hasła to zignoruj tą wiadomość, i zmień hasło!
            """.trimIndent()
    }
}
