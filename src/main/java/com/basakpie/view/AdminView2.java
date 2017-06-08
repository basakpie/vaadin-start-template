package com.basakpie.view;

import com.basakpie.security.User;
import com.basakpie.security.UserRepository;
import com.basakpie.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by basakpie on 2017. 5. 11..
 */
@Secured({"ROLE_ADMIN"})
@SpringView(name = AdminView2.VIEW_NAME)
@SideBarItem(sectionId = Sections.ADMIN, caption = "AdminView2", order = 2)
@VaadinFontIcon(VaadinIcons.ACADEMY_CAP)
public class AdminView2 extends AbstractView {

    public static final String VIEW_NAME = "admin_view_2";

    final UserRepository userRepository;

    @Autowired
    public AdminView2(UserRepository userRepository) {
        this.userRepository = userRepository;
        setSizeFull();
        setViewHeader("AdminView2", VaadinIcons.ACADEMY_CAP);
    }

    @PostConstruct
    public void init() {
        List<User> users = userRepository.findAll();

        Grid<User> grid = new Grid<>();
        grid.addStyleName(ValoTheme.TABLE_COMPACT);
        grid.setSizeFull();
        grid.setItems(users);
        grid.addColumn(User::getUsername).setCaption("Name");
        grid.addColumn(User::getPassword).setCaption("Password");

        addComponent(grid);
        setExpandRatio(grid, 1f);
    }
}

