package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Contributor;
import java.util.List;

/**
 * Data Access Object for Contributor 
 * @author etzoannos
 */
public interface UserAccountDAO {

    /**
     *
     * @param contributor The contributor object of the contributor to be stored
     * @return Boolean value indicating the result of storage action
     */
    public boolean createContributor(Contributor contributor);

    /**
     *
     * @param contributor The contributor object of the contributor to be edited
     * @return Boolean value indicating the result of editing action
     */
    public boolean editContributor(Contributor contributor);

    /**
     *
     * @param contributor The unique identifier of the contributor to be edited
     * @return Boolean value indicating the result of deletion action
     */
    public boolean destroyContributor(Long id);

    /**
     *
     * @param max Number of maximum returned contributors
     * @return Returns List of N most trusted contributors
     */
    public List<Contributor> findTrustedContributors(int max);

    /**
     *
     * @param name The name of the searched contributor
     * @return Returns list of matching contributors
     */
    public List<Contributor> findContributorsByName(String name);

    /**
     *
     * @param id The unique identifier of the contributor
     * @return Returns the matching contributor
     */
    public Contributor findContributorById(Long id);

    /**
     * To be used internally for paging (not to fetch all links on single step)
     *
     * @param id The id of the specific contributor
     * @return Returns the connected contributors
     */
    public List<Contributor> findConnectedContributors(Long id);
}
