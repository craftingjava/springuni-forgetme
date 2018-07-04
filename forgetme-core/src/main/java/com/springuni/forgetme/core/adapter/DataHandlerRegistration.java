package com.springuni.forgetme.core.adapter;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.util.Assert;

@Getter
@Setter
public class DataHandlerRegistration {

  public static final Bindable<Map<String, String>> DATA_HANDLER_PROVIDER_BINDABLE =
      Bindable.mapOf(String.class, String.class);

  public static final String DATA_HANDLER_PROVIDER_PREFIX = "forgetme.data-handler.provider";

  public static final Bindable<DataHandlerRegistration> DATA_HANDLER_REGISTRATION_BINDABLE =
      Bindable.of(DataHandlerRegistration.class);

  public static final String DATA_HANDLER_REGISTRATION_PREFIX = "forgetme.data-handler.registration";

  private String name;
  private String displayName;
  private String description;
  private URI url;
  private Set<DataScope> dataScopes;

  public Optional<URI> getUrl() {
    return Optional.ofNullable(url);
  }

  public void validate() {
    Assert.hasText(getName(), "Data handler name must not be empty.");
    Assert.hasText(getDisplayName(), "Data handler display-name must not be empty.");
    Assert.hasText(getDescription(), "Data handler description must not be empty.");
    Assert.notEmpty(getDataScopes(), "Data handler data-scopes must not be empty.");
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

}
