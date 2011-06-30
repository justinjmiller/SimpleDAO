package org.simpledao;

/**
 * <p/>
 * User: jumiller
 * Date: Jul 13, 2006
 * Time: 9:22:27 AM
 * </p>
 */
public class SimpleDAOConfig
{
    private String beanNameSuffix = "";
    private int nullIntValue = -1;
    private String defaultDatabasePropFile = "database.properties";

    public String getBeanNameSuffix()
    {
        return beanNameSuffix;
    }

    public void setBeanNameSuffix(String beanNameSuffix)
    {
        this.beanNameSuffix = beanNameSuffix;
    }

    public int getNullIntValue()
    {
        return nullIntValue;
    }

    public void setNullIntValue(int nullIntValue)
    {
        this.nullIntValue = nullIntValue;
    }

    public String getDefaultDatabasePropFile()
    {
        return defaultDatabasePropFile;
    }

    public void setDefaultDatabasePropFile(String defaultDatabasePropFile)
    {
        this.defaultDatabasePropFile = defaultDatabasePropFile;
    }
}
