package com.basakpie.config;

import com.vaadin.server.*;
import org.jsoup.nodes.Element;

/**
 * Created by basakpie on 2017-05-26.
 */
public class VaadinSessionInitListener implements SessionInitListener {

    @Override
    public void sessionInit(final SessionInitEvent event)
            throws ServiceException {
        event.getService().setSystemMessagesProvider(
                (SystemMessagesProvider) systemMessagesInfo -> {
                    CustomizedSystemMessages csm = new CustomizedSystemMessages();
                    csm.setSessionExpiredNotificationEnabled(false);
                    return csm;
                });
        event.getSession().addBootstrapListener(new BootstrapListener() {
            @Override
            public void modifyBootstrapPage(final BootstrapPageResponse response) {
                final Element head = response.getDocument().head();
                head.appendElement("meta").attr("name", "viewport")
                        .attr("content", "width=device-width, initial-scale=1");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-capable")
                        .attr("content", "yes");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-status-bar-style")
                        .attr("content", "black");
            }
            @Override
            public void modifyBootstrapFragment(
                    final BootstrapFragmentResponse response) {
                // TODO Auto-generated method stub
            }
        });
    }
}
