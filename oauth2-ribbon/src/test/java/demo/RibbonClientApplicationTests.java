package demo;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RibbonClientApplication.class)
@IntegrationTest("debug=true")
@WebAppConfiguration
public class RibbonClientApplicationTests {

	@Autowired
	@LoadBalanced
	private OAuth2RestTemplate oauth2RestTemplate;

	@Autowired
	@Qualifier("loadBalancedRestTemplate")
	@LoadBalanced
	private RestTemplate restTemplate;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void restTemplateHasLoadBalancer() {
		// Just to prove that the request factory changed...
		assertThat(this.restTemplate.getRequestFactory(),
				instanceOf(RibbonClientHttpRequestFactory.class));
	}

	@Test
	public void oauth2RestTemplateHasLoadBalancer() {
		// Just to prove that the interceptor is present...
		assertThat(new ArrayList<Object>(this.oauth2RestTemplate.getInterceptors()),
				hasItem(instanceOf(LoadBalancerInterceptor.class)));
	}

	@Test
	// FIXME: why is this broken
	@Ignore
	public void useRestTemplate() throws Exception {
		// There's nowhere to get an access token so it should fail, but in a sensible way
		this.expected.expect(OAuth2AccessDeniedException.class);
		this.oauth2RestTemplate.getForEntity("http://foo/bar", String.class);
	}

}
