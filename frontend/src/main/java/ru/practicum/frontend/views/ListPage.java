package ru.practicum.frontend.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("ExploreWithMe")
@Route(value = "list", layout = MainLayout.class)
public final class ListPage extends Div {


    public ListPage() {

        Image image = new Image("https://img.votonia.ru/brands/615ec3dd24ffe.jpg", "https://img.votonia.ru/brands/615ec3dd24ffe.jpg");

        var vertical = new VerticalLayout(image, new H2("test"));
        vertical.setWidth("300");
        vertical.setHeight("300");

        var horizontal = new HorizontalLayout(FlexComponent.Alignment.CENTER, vertical, vertical, vertical);


        var body = new VerticalLayout(horizontal);
        body.setWidth("1000");
        add(body);
    }

}