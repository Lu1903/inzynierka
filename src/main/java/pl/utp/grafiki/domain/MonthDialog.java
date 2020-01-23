package pl.utp.grafiki.domain;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.ThemeList;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.Timezone;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SuppressWarnings("serial")
public class MonthDialog extends Dialog {

    public MonthDialog(FullCalendar calendar, Entry entry, boolean newInstance) {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        CustomDateTimePicker fieldStart = new CustomDateTimePicker("Początek");
        fieldStart.timePicker.setMin("08:30");
        fieldStart.timePicker.setMax("20:30");
        fieldStart.timePicker.setStep(Duration.ofMinutes(30));
        CustomDateTimePicker fieldEnd = new CustomDateTimePicker("Koniec");
        fieldEnd.timePicker.setMin("08:30");
        fieldEnd.timePicker.setMax("20:30");
        fieldEnd.timePicker.setStep(Duration.ofMinutes(30));

        layout.add(fieldStart, fieldEnd);

        Binder<Entry> binder = new Binder<>(Entry.class);
        Timezone timezone = calendar.getTimezone();
        binder.bind(fieldStart, e -> e.getStart(timezone), (e, start) -> e.setStart(start, timezone));
        binder.bind(fieldEnd, e -> e.getEnd(timezone), (e, end) -> e.setEnd(end, timezone));

        binder.setBean(entry);

        
        HorizontalLayout buttons = new HorizontalLayout();
        Button buttonSave;
        if (newInstance) {
            buttonSave = new Button("Utwórz", e -> {
                if (binder.validate().isOk()) {
                	SchedulerHelper.setEntryTitle(entry);
                    calendar.addEntry(entry);
                }
            });
        } else {
            buttonSave = new Button("Zapisz", e -> {
                if (binder.validate().isOk()) {
                	SchedulerHelper.setEntryTitle(entry);
                    calendar.updateEntry(entry);
                }
            });
        }
        buttonSave.addClickListener(e -> close());
        buttons.add(buttonSave);

        Button buttonCancel = new Button("Wyjdź", e -> close());
        buttonCancel.getElement().getThemeList().add("tertiary");
        buttons.add(buttonCancel);

        if (!newInstance) {
            Button buttonRemove = new Button("Usuń"
            		+ "", e -> {
                calendar.removeEntry(entry);
                close();
            });
            ThemeList themeList = buttonRemove.getElement().getThemeList();
            themeList.add("error");
            themeList.add("tertiary");
            buttons.add(buttonRemove);
        }

        add(layout, buttons);
    }

    /**
     * see https://vaadin.com/components/vaadin-custom-field/java-examples
     */
    public static class CustomDateTimePicker extends CustomField<LocalDateTime> {

        private final DatePicker datePicker = new DatePicker();
        private final TimePicker timePicker = new TimePicker();
        private boolean dateOnly;

        CustomDateTimePicker(String label) {
            setLabel(label);
            add(datePicker, timePicker);
        }

        @Override
        protected LocalDateTime generateModelValue() {
            final LocalDate date = datePicker.getValue();
            final LocalTime time = timePicker.getValue();

            if (date != null) {
                if (dateOnly || time == null) {
                    return date.atStartOfDay();
                }

                return LocalDateTime.of(date, time);
            }

            return null;

        }

        @Override
        protected void setPresentationValue(
                LocalDateTime newPresentationValue) {
            datePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalDate() : null);
            timePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalTime() : null);
        }

        public void setDateOnly(boolean dateOnly) {
            this.dateOnly = dateOnly;
            timePicker.setVisible(!dateOnly);
        }
    }

}