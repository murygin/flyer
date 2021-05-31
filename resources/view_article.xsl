<?xml version="1.0"?>
<!--
Styles for viewing article within the flyer mag

@author <a href="mailto:heiko@fork.de">Heiko Braun</a>
@version $Id: view_article.xsl,v 1.4 2001/09/10 13:51:14 heiko Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="no"/>


<!-- i t e m  -->
<xsl:template match="item">
	<span class="copy12">
    <xsl:apply-templates />
    </span>
</xsl:template>

<!-- h e a d i n g
we skip the heading
and retrieve it from db peer
-->
<xsl:template match="heading">

</xsl:template>

<!-- s u b h e a d i n g -->
  <xsl:template match="subheading">
    <i>
      <xsl:value-of select="."/>
    </i>
  </xsl:template>

<!-- p -->

  <xsl:template match="p">
    <p>
     <xsl:apply-templates />
    </p>
  </xsl:template>

<!-- l i / p -->

  <xsl:template match="list/li/p">
    <xsl:apply-templates />
  </xsl:template>

<!-- l i s t -->

  <xsl:template match="list">
    <list>
	    <xsl:apply-templates />
    </list>
  </xsl:template>

<!-- l i -->

  <xsl:template match="li">
    <li>
	    <xsl:apply-templates />
    </li>
  </xsl:template>

<!-- u r l  R e f -->

  <xsl:template match="urlRef">
    <xsl:element name="a">
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="child::url/@protocol='mail'">
            <xsl:text>mailto:</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="child::url/@protocol"/>
            <xsl:text>://</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="url"/>
      </xsl:attribute>
      <xsl:attribute name="target">
        <xsl:text>_blank</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:text>magenta</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="text()"/>
    </xsl:element>
  </xsl:template>

  <!-- p l a i n -->

  <xsl:template match="plain">
  	<xsl:apply-templates/>
  </xsl:template>

<!-- b o l d -->

  <xsl:template match="b">
  	<b><xsl:apply-templates/></b>
  </xsl:template>


  <xsl:template match="i">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="footnote">
    <font size="-1">
      <xsl:apply-templates/>
    </font>
  </xsl:template>

</xsl:stylesheet>
