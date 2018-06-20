package com.springuni.forgetme.datahandler.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Getter
@ConfigurationProperties(prefix = "forgetme.data-handler")
public class DataHandlerProperties {

  /**
   * OAuth provider details.
   */
  private final Map<String, Map<String, String>> provider = new HashMap<>();

  /**
   * OAuth client registrations.
   */
  private final Map<String, Registration> registration = new HashMap<>();

  @PostConstruct
  public void validate() {
    this.getRegistration().values().forEach(this::validateRegistration);
  }

  private void validateRegistration(Registration registration) {
    if (!StringUtils.hasText(registration.getName())) {
      throw new IllegalStateException("Data handler name must not be empty.");
    }
    if (!StringUtils.hasText(registration.getDisplayName())) {
      throw new IllegalStateException("Data handler display-name must not be empty.");
    }
    if (!StringUtils.hasText(registration.getDescription())) {
      throw new IllegalStateException("Data handler description must not be empty.");
    }
    if (!StringUtils.hasText(registration.getUrl())) {
      throw new IllegalStateException("Data handler url must not be empty.");
    }
    if (!CollectionUtils.isEmpty(registration.getDataScopes())) {
      throw new IllegalStateException("Data handler data-scopes must not be empty.");
    }
  }

  public enum DataScope {
    /**
     * The account data may include your name and email address. The source of the account data
     * is the subscriber.
     *
     * The account data may be processed for the purposes of operating websites,
     * providing services, ensuring the security of websites and services, maintaining back-ups of
     * databases and communicating with subscriber.
     */
    ACCOUNT,

    /**
     * Information contained in or relating to any communication that subscribers send. It may
     * include the communication content and metadata associated with the communication.
     *
     * The correspondence data may be processed for the purposes of communicating with subscribers
     * and record-keeping.
     */
    CORRESPONDENCE,

    /**
     * Information contained in any enquiry subscribers submit regarding goods and/or services.
     *
     * The enquiry data may be processed for the purposes of offering, marketing and selling
     * relevant goods and/or services to subscribers.
     */
    ENQUIRY,

    /**
     * Information that subscribers provide for the purpose of subscribing to email notifications
     * and/or newsletters.
     *
     * The notification data may be processed for the purposes of sending subscribers the relevant
     * notifications and/or newsletters.
     */
    NOTIFICATION,

    /**
     * Information included in subscribers' personal profile on websites. The profile data may
     * include your name, address, telephone number, email address, profile pictures, gender,
     * date of birth, relationship status, interests and hobbies, educational details and
     * employment details.
     *
     * The profile data may be processed for the purposes of enabling and monitoring subscribers'
     * use of websites and/or services.
     */
    PROFILE,

    /**
     * Information that subscribers post for publication on websites or through services.
     *
     * The publication data may be processed for the purposes of enabling such publication and
     * administering websites and/or services.
     */
    PUBLICATION,

    /**
     * Data about subscribers' use of websites and/or services. The usage data may include
     * subscribers' IP address, geographical location, browser type and version, operating system,
     * referral source, length of visit, page views and website navigation paths, as well as
     * information about the timing, frequency and pattern of your service use.
     *
     * The sources of the usage data are typically analytic tools (eg. Google Analytics).
     *
     * This usage data may be processed for the purposes of analyzing the use of websites and/or
     * services.
     */
    USAGE;
  }

  /**
   * A single client registration.
   */
  @Getter
  @Setter
  public static class Registration {

    /**
     * Client ID for the registration.
     */
    private String name;

    /**
     * Client secret of the registration.
     */
    private String displayName;

    /**
     * Client authentication method. May be left blank then using a pre-defined
     * provider.
     */
    private String description;

    /**
     * Authorization grant type. May be left blank then using a pre-defined provider.
     */
    private String url;

    /**
     * Authorization scopes. May be left blank then using a pre-defined provider.
     */
    private Set<DataScope> dataScopes;

  }

}
