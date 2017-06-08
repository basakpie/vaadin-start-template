package com.basakpie.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.security.shared.VaadinSharedSecurity;

import javax.annotation.PostConstruct;

/**
 * Created by basakpie on 2017. 5. 11..
 */
@Title("Vaadin Login")
@Theme("valo-default")
@SpringUI(path = "/login")
public class LoginUI extends UI {

    private final VaadinSharedSecurity vaadinSecurity;

    private final Environment environment;

    private TextField username;

    private PasswordField password;

    private CheckBox rememberMe;

    private Button login;

    private Label loginFailedLabel;
    private Label loggedOutLabel;

    @Autowired
    public LoginUI(VaadinSharedSecurity vaadinSecurity, Environment environment) {
        this.vaadinSecurity = vaadinSecurity;
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        addStyleName("login-view");
        Responsive.makeResponsive(this);
    }

    @Override
    protected void init(VaadinRequest request) {
        Component loginLayout = buildLoginLayout(request);
        VerticalLayout rootLayout = new VerticalLayout(loginLayout);
        rootLayout.setSizeFull();
        rootLayout.setMargin(false);
        rootLayout.setSpacing(false);
        rootLayout.setComponentAlignment(loginLayout, Alignment.MIDDLE_CENTER);
        super.setContent(rootLayout);
        super.setSizeFull();
        showNotification();
    }

    private Component buildLoginLayout(VaadinRequest request) {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setMargin(false);
        loginPanel.addStyleName("login-panel");
        Responsive.makeResponsive(loginPanel);

        if (request.getParameter("logout") != null) {
            loggedOutLabel = new Label("You have been logged out!");
            loggedOutLabel.addStyleName(ValoTheme.LABEL_SUCCESS);
            loggedOutLabel.setWidth(100, Unit.PERCENTAGE);
            loginPanel.addComponent(loggedOutLabel);
        }
        loginFailedLabel = new Label();
        loginFailedLabel.setWidth(100, Unit.PERCENTAGE);
        loginFailedLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        loginFailedLabel.setVisible(false);

        loginPanel.addComponent(loginFailedLabel);

        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());
        loginPanel.addComponent(rememberMe = new CheckBox("Remember me", true));
        return loginPanel;
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout();
        labels.addStyleName("labels");

        Label welcome = new Label("Welcome");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        Label title = new Label("Project Template");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.addStyleName("fields");

        username = new TextField("Username");
        username.setIcon(VaadinIcons.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        password = new PasswordField("Password");
        password.setIcon(VaadinIcons.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        login = new Button("Login");
        login.addStyleName(ValoTheme.BUTTON_PRIMARY);
        login.setDisableOnClick(true);
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.focus();

        fields.addComponents(username, password, login);
        fields.setComponentAlignment(login, Alignment.BOTTOM_LEFT);
        login.addClickListener(e -> login());
        return fields;
    }

    private void showNotification() {
        Notification notification = new Notification("Welcome to Project Demo");
        notification.setDescription("<span>This application is not real, it only demonstrates an application built with the <a href=\"https://vaadin.com\" target=\"_blank\">Vaadin framework</a>.</span> <span>No username or password is required, just click the <b>LogIn</b> button to continue.</span>");
        notification.setHtmlContentAllowed(true);
        notification.setStyleName("tray dark small closable login-help");
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.setDelayMsec(20000);
        notification.show(Page.getCurrent());
    }

    private void login() {
        try {
            vaadinSecurity.login(username.getValue(), password.getValue(), rememberMe.getValue());
        } catch (AuthenticationException ex) {
            username.focus();
            username.selectAll();
            password.setValue("");
            loginFailedLabel.setValue(String.format("Login failed: %s", ex.getMessage()));
            loginFailedLabel.setVisible(true);
            if (loggedOutLabel != null) {
                loggedOutLabel.setVisible(false);
            }
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LoggerFactory.getLogger(getClass()).error("Unexpected error while logging in", ex);
        } finally {
            login.setEnabled(true);
        }
    }
}
