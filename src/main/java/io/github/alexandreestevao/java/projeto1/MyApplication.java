/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.alexandreestevao.java.projeto1;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Alexandre
 */
@ApplicationPath("rest")
public class MyApplication extends ResourceConfig { //Extensão do ResourceConfig do Jersey
    public MyApplication(){ //Construtor
        packages("io.github.alexandreestevao.java.projeto1.controllers");
    }
}
