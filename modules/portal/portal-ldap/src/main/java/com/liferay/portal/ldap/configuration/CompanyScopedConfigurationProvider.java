/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.ldap.configuration;

import aQute.bnd.annotation.metatype.Configurable;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.Configuration;

/**
 * @author Michael C. Han
 */
public abstract class CompanyScopedConfigurationProvider
	<T extends CompanyScopedConfiguration> implements ConfigurationProvider<T> {

	@Override
	public T getConfiguration(long companyId) {
		Configuration configuration = _configurations.get(companyId);

		if (configuration == null) {
			configuration = _configurations.get(0L);
		}

		if (configuration == null) {
			Class<?> clazz = getMetatype();

			throw new IllegalArgumentException(
				"No instance of " + clazz.getName() + " for company " +
					companyId);
		}

		Dictionary<String, Object> properties = configuration.getProperties();

		T configurable = Configurable.createConfigurable(
			getMetatype(), properties);

		return configurable;
	}

	@Override
	public T getConfiguration(long companyId, long index) {
		return getConfiguration(companyId);
	}

	@Override
	public List<T> getConfigurations(long companyId) {
		List<T> configurations = new ArrayList<>();

		T t = getConfiguration(companyId);

		configurations.add(t);

		return configurations;
	}

	@Override
	public synchronized void registerConfiguration(
		Configuration configuration) {

		Dictionary<String, Object> properties = configuration.getProperties();

		T configurable = Configurable.createConfigurable(
			getMetatype(), properties);

		long companyId = configurable.companyId();

		_configurations.put(companyId, configuration);
	}

	@Override
	public synchronized void unregisterConfiguration(
		Configuration configuration) {

		Dictionary<String, Object> properties = configuration.getProperties();

		T configurable = Configurable.createConfigurable(
			getMetatype(), properties);

		long companyId = configurable.companyId();

		_configurations.remove(companyId);
	}

	private final Map<Long, Configuration> _configurations = new HashMap<>();

}