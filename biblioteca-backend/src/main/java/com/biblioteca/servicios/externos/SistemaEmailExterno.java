package com.biblioteca.servicios.externos;

public class SistemaEmailExterno {
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        System.out.println("Enviando correo vía API REST externa a " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Cuerpo: " + cuerpo);
    }
}
