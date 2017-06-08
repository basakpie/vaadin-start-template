package com.basakpie.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by basakpie on 2017-05-26.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractView extends VerticalLayout implements View {

    private String title;
    private FontIcon icon;
    private LinkedList<Component> components = new LinkedList();

    public AbstractView() {
        setWidth(100, Unit.PERCENTAGE);
        setHeightUndefined();
        setMargin(false);
        setSpacing(false);
    }

    @Override
    public void attach() {
        super.attach();
        HorizontalLayout toolbar = buildToolbar();
        if(components.size()>0) {
            toolbar.addComponents(components.toArray(new Component[components.size()]));
        }
        addComponentAsFirst(toolbar);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    private HorizontalLayout buildToolbar() {
        if (this.title==null) {
            return null;
        }

        HorizontalLayout header = new HorizontalLayout();
        header.setWidth(100, Unit.PERCENTAGE);
        header.addStyleName("view-header");
        Responsive.makeResponsive(header);

        Label title = new Label();
        title.setContentMode(ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        if(this.icon!=null) {
            title.setValue(this.icon.getHtml() + " " + this.title);
        }  else {
            title.setValue(this.title);
        }
        header.addComponent(title);
        header.setExpandRatio(title, 1.0f);
        header.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        HorizontalLayout tools = new HorizontalLayout();
        tools.setSizeUndefined();
        tools.addComponents(components.toArray(new Component[components.size()]));
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    public void setViewHeader(String title) {
        setViewHeader(title, null);
    }

    public void setViewHeader(String title, VaadinIcons icon) {
        this.title = title;
        this.icon = icon;
    }

    public void addToolbarComponent(Component c) {
        this.components.add(c);
    }

    public void addToolbarComponents(Component... cs) {
        Arrays.stream(cs).forEach(components::add);
    }
}
