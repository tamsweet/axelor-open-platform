/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axelor.rpc.Request;
import com.axelor.rpc.Response;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import org.junit.jupiter.api.Test;

/**
 * Abstract test class for testing REST API resources.
 * <p>
 * This class provides a base for testing API endpoints related to a specific model
 * (e.g., "com.axelor.web.db.Contact"). It includes methods for constructing REST API
 * requests and assertions to validate responses. Subclasses can extend this class
 * to test specific API actions (e.g., search, create, update, delete).
 */
public class ResourceTest extends AbstractTest {

  /**
   * The model identifier for the API resource being tested.
   */
  protected String model = "com.axelor.web.db.Contact";

  /**
   * Constructs an {@link Invocation.Builder} for a REST API request.
   *
   * @param action The action to perform on the resource (e.g., "search", "create", null for base path).
   * @return An {@code Invocation.Builder} for building the request.
   */
  protected Invocation.Builder crud(String action) {
      String path = "/rest/" + model;
      if (action != null) {
          path = path + "/" + action;
      }
      return jsonPath(path);
  }

  /**
   * Tests the API endpoint for retrieving field information for the specified model.
   * <p>
   * This test performs the following checks:
   * <ul>
   *   <li>Sends a GET request to "/meta/fields/{model}".</li>
   *   <li>Asserts that the response is not null.</li>
   *   <li>Asserts that the response data is not null.</li>
   *   <li>Asserts that the response data is a Map.</li>
   *   <li>Asserts that the "model" key in the response data matches the expected model.</li>
   * </ul>
   */
  @Test
  public void testFields() {
      Response response = jsonPath("/meta/fields/" + model).get(Response.class);
      assertNotNull(response);
      assertNotNull(response.getData());
      assertTrue(response.getData() instanceof Map);
      assertEquals(((Map<?, ?>) response.getData()).get("model"), model);
  }

  /**
   * Tests the API endpoint for searching the specified model.
   * <p>
   * This test performs the following checks:
   * <ul>
   *   <li>Sends a POST request to "/rest/{model}/search" with search criteria.</li>
   *   <li>Asserts that the response is not null.</li>
   *   <li>Asserts that the response data is not null.</li>
   *   <li>Asserts that the response data is a List.</li>
   *   <li>Asserts that the response data list is not empty.</li>
   * </ul>
   */
  @Test
  public void testSearch() {
      Request request = new Request();
      request.setData(ImmutableMap.of("firstName", (Object) "John", "lastName", "Teen"));

      Response response = crud("search").post(Entity.json(request), Response.class);
      assertNotNull(response);
      assertNotNull(response.getData());
      assertTrue(response.getData() instanceof List);
      assertTrue(((List<?>) response.getData()).size() > 0);
  }
}
