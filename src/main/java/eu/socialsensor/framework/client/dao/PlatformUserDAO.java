/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.PlatformUser;

/**
 *
 * @author stzoannos
 */
public interface PlatformUserDAO {

    public void addPlatformUser(PlatformUser user);

    public PlatformUser getPlatformUser(String name);

    public void updatePlatformUser(PlatformUser user);
    
}
