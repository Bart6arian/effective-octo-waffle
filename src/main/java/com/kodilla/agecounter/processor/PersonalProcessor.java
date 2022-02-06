package com.kodilla.agecounter.processor;

import com.kodilla.agecounter.domain.Person;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class PersonalProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person item) throws Exception {
        return item.build(
                item.getId(),
                item.getName(),
                item.getLname(),
                years(item));
    }

    private int years(Person i) {
        return i.getDateOfBirth().getYear() - LocalDate.now().getYear();
    }
}
