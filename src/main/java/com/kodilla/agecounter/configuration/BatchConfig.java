package com.kodilla.agecounter.configuration;

import com.kodilla.agecounter.domain.Person;
import com.kodilla.agecounter.processor.PersonalProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    FlatFileItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("file.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("Id ", "Firstname ", "Lastname ", "Date of birth");

        BeanWrapperFieldSetMapper<Person> setMapper = new BeanWrapperFieldSetMapper<>();
        setMapper.setTargetType(Person.class);

        DefaultLineMapper<Person> defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setFieldSetMapper(setMapper);
        defaultLineMapper.setLineTokenizer(tokenizer);

        reader.setLineMapper(defaultLineMapper);
        return reader;
    }

    @Bean
    PersonalProcessor processor() {
        return new PersonalProcessor();
    }

    @Bean
    FlatFileItemWriter<Person> writer() {
        BeanWrapperFieldExtractor<Person> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"Id ", "Firstname ", "Lastname ", "Age "});

        DelimitedLineAggregator<Person> aggregator = new DelimitedLineAggregator();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<Person> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("outputFile.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    Step ageVerifier(
            ItemReader<Person> reader,
            ItemProcessor<Person, Person> processor,
            ItemWriter<Person> writer) {

        return stepBuilderFactory.get("ageVerifier")
                .<Person, Person>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    Job changeDateToAge(Step ageVerifier) {
        return jobBuilderFactory.get("changeDateToAge")
                .incrementer(new RunIdIncrementer())
                .flow(ageVerifier)
                .end()
                .build();
    }
}
