package com.basakpie.ui;

import com.basakpie.sidebar.VaadinSideBar;
import com.basakpie.view.AccessDeniedView;
import com.basakpie.view.ErrorView;
import com.basakpie.view.ViewContainer;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by basakpie on 2017. 5. 11..
 */
@Title("Vaadin Project")
@Theme("valo-default")
@SpringUI(path = "/")
public class MainUI extends UI {

	private final SpringViewProvider springViewProvider;

	private final SpringNavigator springNavigator;

	private final VaadinSideBar vaadinSideBar;

	private final ViewContainer viewContainer;

	@Autowired
	public MainUI(SpringViewProvider springViewProvider, SpringNavigator springNavigator, VaadinSideBar vaadinSideBar, ViewContainer viewContainer) {
		this.springViewProvider = springViewProvider;
		this.springNavigator = springNavigator;
		this.vaadinSideBar = vaadinSideBar;
		this.viewContainer = viewContainer;
	}

	@PostConstruct
	public void init() {
		viewContainer.setSizeFull();
		viewContainer.addStyleName("view-content");
		springNavigator.setErrorView(ErrorView.class);
		springViewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
	}

	@Override
	protected void init(VaadinRequest request){
		addStyleName(ValoTheme.UI_WITH_MENU);
		Responsive.makeResponsive(this);
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.addComponent(vaadinSideBar);
		layout.addComponent(viewContainer);
		layout.setExpandRatio(viewContainer, 1.0f);
		setContent(layout);
	}

}
