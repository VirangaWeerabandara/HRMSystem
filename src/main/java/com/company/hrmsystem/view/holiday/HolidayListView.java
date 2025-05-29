package com.company.hrmsystem.view.holiday;

import com.company.hrmsystem.entity.Holiday;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "holidays", layout = MainView.class)
@ViewController(id = "Holiday.list")
@ViewDescriptor(path = "holiday-list-view.xml")
@LookupComponent("holidaysDataGrid")
@DialogMode(width = "64em")
public class HolidayListView extends StandardListView<Holiday> {
}