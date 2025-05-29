package com.company.hrmsystem.view.holiday;

import com.company.hrmsystem.entity.Holiday;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "holidays/:id", layout = MainView.class)
@ViewController(id = "Holiday.detail")
@ViewDescriptor(path = "holiday-detail-view.xml")
@EditedEntityContainer("holidayDc")
public class HolidayDetailView extends StandardDetailView<Holiday> {
}