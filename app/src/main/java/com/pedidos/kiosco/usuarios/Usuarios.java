package com.pedidos.kiosco.usuarios;

public class Usuarios {

    String nombreUsuario, loginUsuario, passwordUsuarios, passwordRepeatUsuarios, emailUsuario, fechaNac, gSexo;
    int idUsuario, idCargo, idEstado, gEdad, gDui;

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
        this.fechaNac = fechaNac;

    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public String getPasswordUsuarios() {
        return passwordUsuarios;
    }

    public void setPasswordUsuarios(String passwordUsuarios) {
        this.passwordUsuarios = passwordUsuarios;
    }

    public String getPasswordRepeatUsuarios() {
        return passwordRepeatUsuarios;
    }

    public void setPasswordRepeatUsuarios(String passwordRepeatUsuarios) {
        this.passwordRepeatUsuarios = passwordRepeatUsuarios;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

}