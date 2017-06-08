package com.basakpie.view;

import com.basakpie.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import javax.annotation.PostConstruct;

/**
 * Created by basakpie on 2017. 5. 11..
 */
@SpringView(name = UserView1.VIEW_NAME)
@SideBarItem(sectionId = Sections.USER, caption = "UserView1", order = 1)
@VaadinFontIcon(VaadinIcons.ADJUST)
public class UserView1 extends AbstractView {

    public static final String VIEW_NAME = "user_view_1";

    public UserView1() {
        setViewHeader("UserView1", VaadinIcons.ADJUST);
    }

    @PostConstruct
    public void init() {
        addComponent(new Label("Hello, this is user view1."));
    }

}
