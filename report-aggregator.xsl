<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">
    <xsl:template match="node() | @*">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="/">
        <table xsl:version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
            <tr>
                <table>
                    <tr>
                        <td>Test Suite Date: <xsl:value-of select="testsuites/testsuite[@name='JUnit Jupiter']/@timestamp"/></td>
                    </tr>
                    <tr>
                        <td>Test Suite Duration: <xsl:value-of select="testsuites/testsuite[@name='JUnit Jupiter']/@time"/></td>
                    </tr>
                    <tr>
                        <td>Test Suite Total Tests: <xsl:value-of select="testsuites/testsuite[@name='JUnit Jupiter']/@tests"/></td>
                    </tr>
                </table>
            </tr>
            <tr>
                <table>
                    <tr>
                        <th>Test Name</th>
                        <th>Number of Tests</th>
                        <th>Average Test Time</th>
                    </tr>
                    <xsl:for-each-group select="testsuites/testsuite/testcase" group-by="@classname">
                        <tr>
                            <td>
                                <xsl:value-of select="./@classname"/>
                            </td>
                            <td>
                                <xsl:value-of select="count(current-group()/@time)"/>
                            </td>
                            <td>
                                <xsl:value-of
                                    select="sum(current-group()/@time) div count(current-group()/@time)"
                                />
                            </td>
                        </tr>
                    </xsl:for-each-group>
                </table>
            </tr>
        </table>
    </xsl:template>
</xsl:stylesheet>
