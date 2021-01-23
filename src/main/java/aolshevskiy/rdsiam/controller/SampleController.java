package aolshevskiy.rdsiam.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sample")
public class SampleController {
  private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

  private final NamedParameterJdbcOperations jdbcTemplate;

  SampleController(NamedParameterJdbcOperations jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public List<?> read() {
    return jdbcTemplate.queryForList("SELECT * FROM persons", Map.of());
  }

  @PostMapping
  public Map<String, Object> create(@RequestBody  Map<String, Object> request) {
    String firstName = (String) request.get("firstName");
    String lastName = (String) request.get("lastName");

    jdbcTemplate.update(
      "INSERT INTO persons(first_name, last_name) VALUES(:firstName, :lastName)",
      Map.of("firstName", firstName, "lastName", lastName));

    return request;
  }
}
