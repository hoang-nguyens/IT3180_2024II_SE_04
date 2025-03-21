package controllers;

import jakarta.validation.constraints.Email;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import models.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;
import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;

import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;