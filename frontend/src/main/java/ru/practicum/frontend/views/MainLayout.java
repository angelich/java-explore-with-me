package ru.practicum.frontend.views;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;

/**
 * Главный слой для публичной части сайта
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        addHeaderContent();
        createFooter();
    }

    private void addHeaderContent() {
        //если метод станет слишком большой - разбить на маленькие
        // TODO поменять на меню бар https://vaadin.com/docs/latest/components/menu-bar
        var allCategories = new Button("Главная");
        var quests = new Button("Квесты");
        var concerts = new Button("Концерты");
        var active = new Button("Активный отдых");


        quests.addClickListener(e ->
                quests.getUI().ifPresent(ui ->
                        ui.navigate("list")));


        var search = new TextField();
        search.setTooltipText("Введите интересующее вас событие");
        search.setPlaceholder("Поиск событий");
        search.setClearButtonVisible(true);

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE);

        var categories = new HorizontalLayout(CENTER, viewTitle, allCategories, quests, concerts, active, search);
        categories.setPadding(true);
        categories.setMaxWidth("1000");
        categories.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);


        var verticalLayout = new VerticalLayout(FlexComponent.Alignment.CENTER, categories);
        addToNavbar(true, verticalLayout);
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
