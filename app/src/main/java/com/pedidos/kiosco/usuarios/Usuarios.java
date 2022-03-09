package com.pedidos.kiosco.usuarios;

public class Usuarios {

    String nombreUsuario, loginUsuario, passwordUsuarios, passwordRepeatUsuarios, emailUsuario;
    int idUsuario, idCargo, idEstado;

    public Usuarios(){
    }

    public Usuarios(int idUsuario, String nombreUsuario, String loginUsuario, String passwordUsuarios, String passwordRepeatUsuarios,
                    String emailUsuario, int idCargo, int idEstado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.loginUsuario = loginUsuario;
        this.passwordUsuarios = passwordUsuarios;
        this.passwordRepeatUsuarios = passwordRepeatUsuarios;
        this.emailUsuario = emailUsuario;
        this.idCargo = idCargo;
        this.idEstado = idEstado;

    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public String getPasswordUsuarios() {
        return passwordUsuarios;
    }

    public String getPasswordRepeatUsuarios() {
        return passwordRepeatUsuarios;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public int getIdEstado() {
        return idEstado;
    }

}