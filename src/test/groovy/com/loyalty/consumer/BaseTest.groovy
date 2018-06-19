package com.loyalty.consumer

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("integration")
@SpringBootTest
class BaseTest extends Specification {

}
