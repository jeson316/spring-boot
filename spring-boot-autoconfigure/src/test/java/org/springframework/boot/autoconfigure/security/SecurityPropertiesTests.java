/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SecurityProperties}.
 *
 * @author Dave Syer
 */
public class SecurityPropertiesTests {

	private SecurityProperties security = new SecurityProperties();

	private RelaxedDataBinder binder = new RelaxedDataBinder(this.security, "security");

	@Before
	public void init() {
		this.binder.setIgnoreUnknownFields(false);
		this.binder.setConversionService(new DefaultConversionService());
	}

	@Test
	public void testBindingIgnoredSingleValued() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.ignored", "/css/**")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getIgnored()).hasSize(1);
	}

	@Test
	public void testBindingIgnoredEmpty() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.ignored", "")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getIgnored()).isEmpty();
	}

	@Test
	public void testBindingIgnoredDisable() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.ignored", "none")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getIgnored()).hasSize(1);
	}

	@Test
	public void testBindingIgnoredMultiValued() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.ignored", "/css/**,/images/**")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getIgnored()).hasSize(2);
	}

	@Test
	public void testBindingIgnoredMultiValuedList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("security.ignored[0]", "/css/**");
		map.put("security.ignored[1]", "/foo/**");
		this.binder.bind(new MutablePropertyValues(map));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getIgnored()).hasSize(2);
		assertThat(this.security.getIgnored().contains("/foo/**")).isTrue();
	}

	@Test
	public void testDefaultPasswordAutogeneratedIfUnresolvedPlaceholder() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.user.password", "${ADMIN_PASSWORD}")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getUser().isDefaultPassword()).isTrue();
	}

	@Test
	public void testDefaultPasswordAutogeneratedIfEmpty() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.user.password", "")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getUser().isDefaultPassword()).isTrue();
	}

	@Test
	public void testRoles() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.user.role", "USER,ADMIN")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getUser().getRole().toString())
				.isEqualTo("[USER, ADMIN]");
	}

	@Test
	public void testRole() {
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.user.role", "ADMIN")));
		assertThat(this.binder.getBindingResult().hasErrors()).isFalse();
		assertThat(this.security.getUser().getRole().toString()).isEqualTo("[ADMIN]");
	}

	@Test
	public void testCsrf() {
		assertThat(this.security.isEnableCsrf()).isEqualTo(false);
		this.binder.bind(new MutablePropertyValues(
				Collections.singletonMap("security.enable-csrf", true)));
		assertThat(this.security.isEnableCsrf()).isEqualTo(true);
	}

}
