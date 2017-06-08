package com.basakpie.sidebar;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.sidebar.SideBarItemDescriptor;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.SideBarUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by basakpie on 2017-05-26.
 */
@SpringComponent
@UIScope
public class VaadinSideBar extends CustomComponent {

    private final VaadinSecurity vaadinSecurity;

    private final SpringNavigator springNavigator;

    private final Environment environment;

    private final SideBarUtils sideBarUtils;

    private SectionComponentFactory<CssLayout> sectionComponentFactory;
    private ItemComponentFactory itemComponentFactory;
    private ItemFilter itemFilter;

    private static final String VALO_MENU_VISIBLE  = "valo-menu-visible";
    private static final String VALO_MENU_TOGGLE   = "valo-menu-toggle";

    @Autowired
    public VaadinSideBar(SideBarUtils sideBarUtils, VaadinSecurity vaadinSecurity, SpringNavigator springNavigator, Environment environment) {
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        setSizeUndefined();
        this.sideBarUtils = sideBarUtils;
        this.vaadinSecurity = vaadinSecurity;
        this.springNavigator = springNavigator;
        this.environment = environment;
    }

    @Override
    public void attach() {
        super.attach();
        CssLayout menuPart = createMenuPart();
        setCompositionRoot(menuPart);

        CssLayout menuItems = createMenuItems(menuPart);
        menuPart.addComponent(menuItems);

        CssLayout systemItems = createSystemItems();
        menuItems.addComponent(systemItems);
    }

    private CssLayout createMenuPart() {
        final CssLayout layout = new CssLayout();
        layout.addStyleName(ValoTheme.MENU_PART);

        final Label title = new Label("<h3>Vaadin <strong>Project</strong></h3>", ContentMode.HTML);
        title.setSizeUndefined();

        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.addComponent(title);

        layout.addComponent(top);

        // 반응형 웹 메뉴네비버튼
        final Button showMenu = new Button("Menu", event -> {
            // 버튼을 클릭하면 toggle 형태로 메뉴파트레이아웃이 보였다 안 보였다 처리
            if (layout.getStyleName().contains(VALO_MENU_VISIBLE)) {
                layout.removeStyleName(VALO_MENU_VISIBLE);
            } else {
                layout.addStyleName(VALO_MENU_VISIBLE);
            }
        });

        // 메뉴네비아이콘 스타일 적용
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_TINY);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(VaadinIcons.MENU);

        layout.addComponent(showMenu);

        return layout;
    }

    private CssLayout createMenuItems(CssLayout menuPart) {
        CssLayout menuItems = new CssLayout();
        menuItems.setPrimaryStyleName("valo-menuitems");
        for (SideBarSectionDescriptor section : sideBarUtils.getSideBarSections(getUI().getClass())) {
            createSection(menuPart, menuItems, section, sideBarUtils.getSideBarItems(section));
        }
        return menuItems;
    }

    private CssLayout createSystemItems() {
        Button logout = new Button("Logout", event -> vaadinSecurity.logout());
        logout.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        logout.setIcon(VaadinIcons.SIGN_OUT);

        CssLayout systemItems = new CssLayout();
        systemItems.addStyleName("valo-systemitems");
        systemItems.addComponent(logout);
        return systemItems;
    }


    private void createSection(CssLayout menuPart, CssLayout menuItems, SideBarSectionDescriptor section, Collection<SideBarItemDescriptor> items) {
        if (items.isEmpty()) {
            return;
        }
        if (getItemFilter() == null) {
            getSectionComponentFactory().createSection(menuPart, menuItems, section, items);
        } else {
            List<SideBarItemDescriptor> passedItems = new ArrayList<>();
            for (SideBarItemDescriptor candidate : items) {
                if (getItemFilter().passesFilter(candidate)) {
                    passedItems.add(candidate);
                }
            }
            if (!passedItems.isEmpty()) {
                getSectionComponentFactory().createSection(menuPart, menuItems, section, passedItems);
            }
        }
    }

    protected SectionComponentFactory<CssLayout> getSectionComponentFactory() {
        if (sectionComponentFactory == null) {
            sectionComponentFactory = createDefaultSectionComponentFactory();
        }
        sectionComponentFactory.setItemComponentFactory(getItemComponentFactory());
        return sectionComponentFactory;
    }

    protected void setSectionComponentFactory(SectionComponentFactory<CssLayout> sectionComponentFactory) {
        this.sectionComponentFactory = sectionComponentFactory;
    }

    protected ItemComponentFactory getItemComponentFactory() {
        if (itemComponentFactory == null) {
            itemComponentFactory = createDefaultItemComponentFactory();
        }
        return itemComponentFactory;
    }

    public void setItemFilter(ItemFilter itemFilter) {
        if (isAttached()) {
            throw new IllegalStateException("An ItemFilter cannot be set when the SideBar is attached");
        }
        this.itemFilter = itemFilter;
    }

    public ItemFilter getItemFilter() {
        return itemFilter;
    }

    protected SectionComponentFactory<CssLayout> createDefaultSectionComponentFactory() {
        return new DefaultSectionComponentFactory();
    }

    public class DefaultSectionComponentFactory implements SectionComponentFactory<CssLayout> {

        private ItemComponentFactory itemComponentFactory;

        @Override
        public void setItemComponentFactory(ItemComponentFactory itemComponentFactory) {
            this.itemComponentFactory = itemComponentFactory;
        }

        @Override
        public void createSection(CssLayout menuPart, CssLayout menuItems, SideBarSectionDescriptor descriptor, Collection<SideBarItemDescriptor> itemDescriptors) {
            if(!descriptor.getId().equals(Sections.NO_GROUP)) {
                Label header = new Label();
                header.setValue(descriptor.getCaption());
                header.setSizeUndefined();
                header.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                menuItems.addComponent(header);
            }
            for (SideBarItemDescriptor item : itemDescriptors) {
                menuItems.addComponent(itemComponentFactory.createItemComponent(menuPart, item));
            }
        }
    }

    public interface SectionComponentFactory<CR extends ComponentContainer> {
        void setItemComponentFactory(ItemComponentFactory itemComponentFactory);
        void createSection(CR menuPart, CR menuItems, SideBarSectionDescriptor descriptor, Collection<SideBarItemDescriptor> itemDescriptors);
    }

    protected ItemComponentFactory createDefaultItemComponentFactory() {
        return new DefaultItemComponentFactory();
    }

    public class DefaultItemComponentFactory implements ItemComponentFactory {
        @Override
        public Component createItemComponent(CssLayout menuPart, SideBarItemDescriptor descriptor) {
            if (descriptor instanceof SideBarItemDescriptor.ViewItemDescriptor) {
                return new ViewItemButton(menuPart, (SideBarItemDescriptor.ViewItemDescriptor) descriptor);
            } else {
                return new ItemButton(descriptor);
            }
        }
    }

    public interface ItemComponentFactory {
        Component createItemComponent(CssLayout layout, SideBarItemDescriptor descriptor);
    }

    static class ViewItemButton extends ItemButton implements ViewChangeListener {

        private final String viewName;
        private final CssLayout menuPart;
        private static final String STYLE_SELECTED = "selected";

        ViewItemButton(CssLayout menuPart, SideBarItemDescriptor.ViewItemDescriptor descriptor) {
            super(descriptor);
            this.menuPart = menuPart;
            this.viewName = descriptor.getViewName();
        }

        @Override
        public void attach() {
            super.attach();
            if (getUI().getNavigator() == null) {
                throw new IllegalStateException("Please configure the Navigator before you attach the SideBar to the UI");
            }
            getUI().getNavigator().addViewChangeListener(this);
        }

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            if (event.getViewName().equals(viewName)) {
                addStyleName(STYLE_SELECTED);
            } else {
                removeStyleName(STYLE_SELECTED);
            }
            menuPart.removeStyleName(VALO_MENU_VISIBLE);
        }
    }

    static class ItemButton extends Button {

        ItemButton(final SideBarItemDescriptor descriptor) {
            setPrimaryStyleName(ValoTheme.MENU_ITEM);
            setCaption(descriptor.getCaption());
            setIcon(descriptor.getIcon());
            setId(descriptor.getItemId());
            setDisableOnClick(true);
            addClickListener(new Button.ClickListener() {

                private static final long serialVersionUID = -8512905888847432801L;

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        descriptor.itemInvoked(getUI());
                    } finally {
                        setEnabled(true);
                    }
                }

            });
        }
    }

    public interface ItemFilter {
        boolean passesFilter(SideBarItemDescriptor descriptor);
    }
}
