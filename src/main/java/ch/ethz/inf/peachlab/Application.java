package ch.ethz.inf.peachlab;

import ch.ethz.inf.peachlab.backend.repository.BaseRepositoryImpl;
import ch.ethz.inf.peachlab.backend.repository.CustomRepositoryFactoryBean;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "ch.ethz.inf.peachlab.model.entity")
@EnableJpaRepositories(
        basePackageClasses = BaseRepositoryImpl.class,
        repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class)
@SpringBootApplication
@Push
@Theme("kaggle-vis")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
