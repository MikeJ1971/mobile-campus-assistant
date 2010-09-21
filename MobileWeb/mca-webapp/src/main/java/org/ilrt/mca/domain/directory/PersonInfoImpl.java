/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.domain.directory;

import com.google.gson.annotations.Expose;

/**
 * @author Jasper Tredgold (jasper.tredgold@bristol.ac.uk)
 */
public class PersonInfoImpl implements PersonInfo {

    @Expose
    private String person_key;
    @Expose
    private String email;
    @Expose
    private String family_name;
    @Expose
    private String given_name;
    @Expose
    private String job_title;
    @Expose
    private String org_unit;
    @Expose
    private String post_code;
    @Expose
    private String[] address;
    @Expose
    private String telephone;
    @Expose
    private String title;

    @Override
    public String getPersonKey() {
        return person_key;
    }

    public void setPersonKey(String v) {
        person_key = v;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    @Override
    public String getFamilyName() {
        return family_name;
    }

    public void setFamilyName(String v) {
        family_name = v;
    }

    @Override
    public String getGivenName() {
        return given_name;
    }

    public void setGivenName(String v) {
        given_name = v;
    }

    @Override
    public String getJobTitle() {
        return job_title;
    }

    public void setJobTitle(String v) {
        job_title = v;
    }

    @Override
    public String getOrganizationUnit() {
        return org_unit;
    }

    public void setOrganizationUnit(String v) {
        org_unit = v;
    }

    @Override
    public String getPostCode() {
        return post_code;
    }

    public void setPostCode(String v) {
        post_code = v;
    }

    @Override
    public String[] getStreetAddress() {
        return address;
    }

    public void setStreetAddress(String[] v) {
        address = v;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String v) {
        telephone = v;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

}
