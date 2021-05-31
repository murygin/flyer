<?xml version="1.0"?>
<!--
This is the basic stylesheet for edition memo object,
basically it only wraps around the memo body
which is treated the same way as an article.
an item.

@author <a href="mailto:heiko@fork.de">Heiko Braun</a>
@version $id$
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="no"/>


<!-- i t e m  -->
<xsl:template match="item">

<!--  Author: <xsl:value-of select="@author"/><br/>
  Date: <xsl:value-of select="substring(@modify_stamp,0,11)"/><br/>
  Lang: <xsl:value-of select="@lang"/><br/>-->

 
    <xsl:apply-templates />
 
</xsl:template>

<!-- h e a d i n g -->

  <xsl:template match="heading">
       <xsl:apply-templates />
  </xsl:template>

<!-- s u b h e a d i n g -->

  <xsl:template match="subheading">
       <xsl:apply-templates />
  </xsl:template>

<!-- p -->

  <xsl:template match="p">
     
      <xsl:apply-templates />
    
  </xsl:template>

<!-- l i / p -->

  <xsl:template match="list/li/p">
    <xsl:apply-templates />
  </xsl:template>

<!-- l i s t -->

  <xsl:template match="list">
	<xsl:apply-templates />
  </xsl:template>

<!-- l i -->

  <xsl:template match="li">
	<xsl:apply-templates />
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
          <xsl:value-of select="text()"/>
        </xsl:element>

   
  </xsl:template>

<!-- u r l -->

  <xsl:template match="url">
    <xsl:text> [</xsl:text>
    <xsl:value-of select="@protocol"/>
    <xsl:choose>
      <xsl:when test="@protocol='mail'">
        <xsl:text>:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>://</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:value-of select="."/>
    <xsl:text>]</xsl:text>
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
      <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
