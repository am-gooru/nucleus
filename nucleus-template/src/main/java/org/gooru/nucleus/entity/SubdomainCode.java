package org.gooru.nucleus.entity;

/**
 * @author Sachin
 * 
 *         Used to store mapping between taxonomy subdomain and standards
 */
public class SubdomainCode {

  private int subdomainId;

  private int codeId;

  public int getSubdomainId() {
    return subdomainId;
  }

  public void setSubdomainId(int subdomainId) {
    this.subdomainId = subdomainId;
  }

  public int getCodeId() {
    return codeId;
  }

  public void setCodeId(int codeId) {
    this.codeId = codeId;
  }

}
