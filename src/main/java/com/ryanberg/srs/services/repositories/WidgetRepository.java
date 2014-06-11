package com.ryanberg.srs.services.repositories;

import com.ryanberg.srs.domain.Widget;
import org.springframework.data.repository.CrudRepository;


public interface WidgetRepository extends CrudRepository<Widget, Long>
{
}
