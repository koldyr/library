package com.koldyr.library

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

/**
 * Description of class MethodSecurityConfig
 * @created: 2021-10-27
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
open class MethodSecurityConfig: GlobalMethodSecurityConfiguration()
