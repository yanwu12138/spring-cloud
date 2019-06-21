package com.yanwu.spring.cloud.common.core.jmx.health;


/**
 * It will collect health info at component level.
 *  
 * 
 *
 */
public interface HealthCollector extends FaultPointerSupport {

	/**
	 * Component Name, should be unified with others. 
	 * If it is duplicated with another one, <code>HealthCollectorRegistry.registry()</code> will throw exception
	 * @return
	 * The name of component
	 */
	String getComponentName();
	
	/**
	 * Touch detail services and build component health.
	 * 
	 * Do not create new instance of <code> ComponentHealth </code>, 
	 * re-use <code>AbstractHealthCollector.health</code>
	 * @return
	 */
	ComponentHealth collect();
	
}
