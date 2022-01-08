package com.pedidos.kiosco;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private final Map<String, String> params;

    public static final String URL = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/loginIngreso.php";

    public LoginRequest(String username, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("login_usuario", username);
        params.put("password_usuarios", password);

    }

    @Override
    protected Map<String, String> getParams(){
        return params;
 }
}
