/*
 * CommandLineOptions.java
 *
 * Copyright (c) 2013 Mike Strobel
 *
 * This source code is based on Mono.Cecil from Jb Evain, Copyright (c) Jb Evain;
 * and ILSpy/ICSharpCode from SharpDevelop, Copyright (c) AlphaSierraPapa.
 *
 * This source code is subject to terms and conditions of the Apache License, Version 2.0.
 * A copy of the license can be found in the License.html file at the root of this distribution.
 * By using this source code in any fashion, you are agreeing to be bound by the terms of the
 * Apache License, Version 2.0.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.strobel.decompiler;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CommandLineOptions {
    @Parameter(description = "<type names or class/jar files>")
    private final List<String> _inputs = new ArrayList<>();

    @Parameter(
        names = { "-?", "--help" },
        help = true,
        description = "Bu kullanım bilgilerini görüntüler ve çıkış yapar.")
    private boolean _printUsage;

    @Parameter(
        names = { "-mv", "--merge-variables" },
        description = "Mümkün olduğunca çok değişkeni birleştirmeye çalışır. Bu daha az değişken bildirimi " +
                      "sağlayabilir ancak satır içi ekleme (inlining) ve anlamlı adlandırma pahasına olur. " +
                      "Bu özellik deneyseldir; gelecek sürümlerde kaldırılabilir veya standart davranış haline gelebilir.")
    private boolean _mergeVariables;

    @Parameter(
        names = { "-ei", "--explicit-imports" },
        description = "[DEPRECIATED - KALDIRILACAK] Açık içe aktarmalar (explicit imports) artık varsayılan olarak etkindir. " +
                      "Bu seçenek gelecek bir sürümde tamamen kaldırılacaktır.")
    private boolean _forceExplicitImports;

    @Parameter(
        names = { "-ci", "--collapse-imports" },
        description = "Aynı paketten yapılan birden çok içe aktarmayı tek bir joker karakterli (*) içe aktarmada birleştirir.")
    private boolean _collapseImports;

    @Parameter(
        names = { "-eta", "--explicit-type-arguments" },
        description = "Genel (generic) metodlar için tür argümanlarını her zaman açıkça yazdırır.")
    private boolean _forceExplicitTypeArguments;

    @Parameter(
        names = { "-ec", "--retain-explicit-casts" },
        description = "Gereksiz olan açık tür dönüşümlerini (explicit casts) koddan temizlemez, olduğu gibi korur.")
    private boolean _retainRedundantCasts;

    @Parameter(
        names = { "-fsb", "--flatten-switch-blocks" },
        description = "Mümkün olduğunda switch bölümlerinin etrafındaki süslü parantez ({}) ifadelerini kaldırıp düzleştirir.")
    private boolean _flattenSwitchBlocks;

    @Parameter(
        names = { "-ss", "--show-synthetic" },
        description = "Sentetik (derleyici tarafından otomatik üretilen) üyeleri kodda gösterir.")
    private boolean _showSyntheticMembers;

    @Parameter(
        names = { "-b", "--bytecode-ast" },
        description = "Java kodu yerine Bytecode AST (Soyut Sözdizimi Ağacı) çıktısı verir.")
    private boolean _bytecodeAst;

    @Parameter(
        names = { "-r", "--raw-bytecode" },
        description = "Java yerine Ham Bytecode çıktısı verir (detay seviyesini kontrol etmek için bkz: -cp, -lv, -ta, -v).")
    private boolean _rawBytecode;

    @Parameter(
        names = { "-cp", "--constant-pool" },
        description = "Ham bytecode görüntülenirken sabit havuzunu (constant pool) da dahil eder (-v ile kullanıldığında buna gerek yoktur).")
    private boolean _showConstantPool;

    @Parameter(
        names = { "-lv", "--local-variables" },
        description = "Ham bytecode görüntülenirken yerel değişken tablolarını dahil eder (-v ile kullanıldığında buna gerek yoktur).")
    private boolean _showLocalVariableDetails;

    @Parameter(
        names = { "-ta", "--type-attributes" },
        description = "Ham bytecode görüntülenirken tür niteliklerini dahil eder (-v ile kullanıldığında buna gerek yoktur).")
    private boolean _showTypeAttributes;

    @Parameter(
        names = { "-v", "--verbose" },
        description = "Seçilen çıktı diline bağlı olarak daha ayrıntılı çıktı içerir (şu anda yalnızca ham bytecode için desteklenmektedir).")
    private boolean _verbose;

    @Parameter(
        names = { "-u", "--unoptimized" },
        description = "Optimize edilmemiş kodu gösterir (yalnızca -b seçeneği ile birlikte çalışır).")
    private boolean _unoptimized;

    @Parameter(
        names = { "-ent", "--exclude-nested" },
        description = "İç içe geçmiş (nested) türleri, onları çevreleyen ana türler decompile edilirken hariç tutar.")
    private boolean _excludeNestedTypes;

    @Parameter(
        names = { "-o", "--output-directory" },
        description = "Decompile edilen sonuçları konsola yazdırmak yerine belirtilen dizine/klasöre yazar.")
    private String _outputDirectory;

    @Parameter(
        names = { "-jar", "--jar-file" },
        description = "[DEPRECIATED - KALDIRILACAK] Belirtilen jar dosyasındaki tüm sınıfları decompile eder (-ent ve -s seçeneklerini devre dışı bırakır).")
    private String _jarFile;

    @Parameter(
        names = { "-ln", "--with-line-numbers" },
        description = "Ham bytecode modunda satır numaralarını dahil eder; Java modunda ise yalnızca -o parametresiyle birlikte desteklenir.")
    private boolean _includeLineNumbers;

    @Parameter(
        names = { "-sl", "--stretch-lines" },
        description = "Java satırlarını orijinal satır numaralarıyla eşleşecek şekilde uzatır (yalnızca -o ile birlikte çalışır) [DENEYSEL].")
    private boolean _stretchLines;

    @Parameter(
        names = { "-dl", "--debug-line-numbers" },
        description = "Hata ayıklama için, Java satır numaralarını satır içi yorum satırı olarak gösterir (-ln durumunu tetikler; -o gerektirir).")
    private boolean _showDebugLineNumbers;

    @Parameter(
        names = { "-ps", "--retain-pointless-switches" },
        description = "Yalnızca 'default' etiketi olan (başka case'i bulunmayan) switch bloklarının içeriğini dışarı çıkarmaz, olduğu gibi korur.")
    private boolean _retainPointlessSwitches;

    @Parameter(
        names = { "-ll", "--log-level" },
        description = "Log ayrıntı düzeyini belirler (0-3 arası değer alır). Seviye 0 loglamayı tamamen kapatır.",
        arity = 1)
    private int _logLevel;

    @Parameter(
        names = { "-lc", "--light" },
        description = "Açık renkli arka plana sahip konsollar için tasarlanmış bir renk şeması kullanır.")
    private boolean _useLightColorScheme;

    @Parameter(
        names = { "--unicode" },
        description = "Unicode çıktısını etkinleştirir (yazdırılabilir ASCII dışı karakterler dönüştürülmeden olduğu gibi kalır).")
    private boolean _isUnicodeOutputEnabled;

    @Parameter(
        names = { "-eml", "--eager-method-loading" },
        description = "Metod gövdelerinin önceden (eager) yüklenmesini etkinleştirir (büyük arşivlerin decompile hızını artırabilir).")
    private boolean _isEagerMethodLoadingEnabled;

    @Parameter(
        names = { "-sm", "--simplify-member-references" },
        description = "Java çıktısındaki türe göre nitelenmiş üye referanslarını basitleştirir [DENEYSEL].")
    private boolean _simplifyMemberReferences;

    @Parameter(
        names = { "-fq", "--force-qualified-references" },
        description = "Java çıktısında her zaman tam nitelikli (fully qualified) tür ve üye referansları kullanmaya zorlar.")
    private boolean _forceFullyQualifiedReferences;

    @Parameter(
        names = { "--disable-foreach" },
        description = "'for each' döngü dönüşümlerini devre dışı bırakır.")
    private boolean _disableForEachTransforms;

    @Parameter(
        names = { "--version" },
        description = "Decompiler versiyonunu görüntüler ve çıkış yapar.")
    private boolean _printVersion;

    @Parameter(
        names = { "--suppress-banner" },
        description = "Çıktı dosyalarında 'Decompiled by Procyon' başlık yazısının gösterilmesini engeller.",
        hidden = true)

    private boolean _suppressBanner;

    public final List<String> getInputs() {
        return _inputs;
    }

    public final boolean isBytecodeAst() {
        return _bytecodeAst;
    }

    public final boolean isRawBytecode() {
        return _rawBytecode;
    }

    public final boolean isVerbose() {
        return _verbose;
    }

    public final boolean getShowConstantPool() {
        return _showConstantPool;
    }

    public final boolean getShowLocalVariableDetails() {
        return _showLocalVariableDetails;
    }

    public final boolean getShowTypeAttributes() {
        return _showTypeAttributes;
    }

    public final boolean getFlattenSwitchBlocks() {
        return _flattenSwitchBlocks;
    }

    public final boolean getExcludeNestedTypes() {
        return _excludeNestedTypes;
    }

    public final void setExcludeNestedTypes(final boolean excludeNestedTypes) {
        _excludeNestedTypes = excludeNestedTypes;
    }

    public final void setFlattenSwitchBlocks(final boolean flattenSwitchBlocks) {
        _flattenSwitchBlocks = flattenSwitchBlocks;
    }

    public final boolean getCollapseImports() {
        return _collapseImports;
    }

    public final void setCollapseImports(final boolean collapseImports) {
        _collapseImports = collapseImports;
    }

    public final boolean getForceExplicitTypeArguments() {
        return _forceExplicitTypeArguments;
    }

    public final void setForceExplicitTypeArguments(final boolean forceExplicitTypeArguments) {
        _forceExplicitTypeArguments = forceExplicitTypeArguments;
    }

    public boolean getRetainRedundantCasts() {
        return _retainRedundantCasts;
    }

    public void setRetainRedundantCasts(final boolean retainRedundantCasts) {
        _retainRedundantCasts = retainRedundantCasts;
    }

    public final void setRawBytecode(final boolean rawBytecode) {
        _rawBytecode = rawBytecode;
    }

    public final void setBytecodeAst(final boolean bytecodeAst) {
        _bytecodeAst = bytecodeAst;
    }

    public final boolean isUnoptimized() {
        return _unoptimized;
    }

    public final void setUnoptimized(final boolean unoptimized) {
        _unoptimized = unoptimized;
    }

    public final boolean getShowSyntheticMembers() {
        return _showSyntheticMembers;
    }

    public final void setShowSyntheticMembers(final boolean showSyntheticMembers) {
        _showSyntheticMembers = showSyntheticMembers;
    }

    public final boolean getPrintUsage() {
        return _printUsage;
    }

    public final void setPrintUsage(final boolean printUsage) {
        _printUsage = printUsage;
    }

    public final String getOutputDirectory() {
        return _outputDirectory;
    }

    public final void setOutputDirectory(final String outputDirectory) {
        _outputDirectory = outputDirectory;
    }

    public final String getJarFile() {
        return _jarFile;
    }

    public final void setJarFile(final String jarFile) {
        _jarFile = jarFile;
    }

    public final boolean getIncludeLineNumbers() {
        return _includeLineNumbers;
    }

    public final void setIncludeLineNumbers(final boolean includeLineNumbers) {
        _includeLineNumbers = includeLineNumbers;
    }

    public final boolean getStretchLines() {
        return _stretchLines;
    }

    public final void setStretchLines(final boolean stretchLines) {
        _stretchLines = stretchLines;
    }

    public final boolean getShowDebugLineNumbers() {
        return _showDebugLineNumbers;
    }

    public final void setShowDebugLineNumbers(final boolean showDebugLineNumbers) {
        _showDebugLineNumbers = showDebugLineNumbers;
    }

    public final boolean getRetainPointlessSwitches() {
        return _retainPointlessSwitches;
    }

    public final void setRetainPointlessSwitches(final boolean retainPointlessSwitches) {
        _retainPointlessSwitches = retainPointlessSwitches;
    }

    public final int getLogLevel() {
        return _logLevel;
    }

    public final void setLogLevel(final int logLevel) {
        _logLevel = logLevel;
    }

    public final boolean getUseLightColorScheme() {
        return _useLightColorScheme;
    }

    public final void setUseLightColorScheme(final boolean useLightColorScheme) {
        _useLightColorScheme = useLightColorScheme;
    }

    public final boolean isUnicodeOutputEnabled() {
        return _isUnicodeOutputEnabled;
    }

    public final void setUnicodeOutputEnabled(final boolean unicodeOutputEnabled) {
        _isUnicodeOutputEnabled = unicodeOutputEnabled;
    }

    public final boolean getMergeVariables() {
        return _mergeVariables;
    }

    public final void setMergeVariables(final boolean mergeVariables) {
        _mergeVariables = mergeVariables;
    }

    public final boolean isEagerMethodLoadingEnabled() {
        return _isEagerMethodLoadingEnabled;
    }

    public final void setEagerMethodLoadingEnabled(final boolean isEagerMethodLoadingEnabled) {
        _isEagerMethodLoadingEnabled = isEagerMethodLoadingEnabled;
    }

    public final boolean getSimplifyMemberReferences() {
        return _simplifyMemberReferences;
    }

    public final void setSimplifyMemberReferences(final boolean simplifyMemberReferences) {
        _simplifyMemberReferences = simplifyMemberReferences;
    }

    public boolean getForceFullyQualifiedReferences() {
        return _forceFullyQualifiedReferences;
    }

    public void setForceFullyQualifiedReferences(final boolean forceFullyQualifiedReferences) {
        _forceFullyQualifiedReferences = forceFullyQualifiedReferences;
    }

    public final boolean getDisableForEachTransforms() {
        return _disableForEachTransforms;
    }

    public final void setDisableForEachTransforms(final boolean disableForEachTransforms) {
        _disableForEachTransforms = disableForEachTransforms;
    }

    public final boolean getPrintVersion() {
        return _printVersion;
    }

    public final void setPrintVersion(final boolean printVersion) {
        _printVersion = printVersion;
    }

    public final boolean getSuppressBanner() {
        return _suppressBanner;
    }

    public final void setSuppressBanner(final boolean suppressBanner) {
        _suppressBanner = suppressBanner;
    }
}
