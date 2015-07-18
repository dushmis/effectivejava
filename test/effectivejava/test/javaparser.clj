(ns effectivejava.test.javaparser
  (:use [effectivejava.core])
  (:use [effectivejava.itemsOnLifecycle])
  (:use [effectivejava.model.protocols])
  (:use [effectivejava.model.javaparser])
  (:use [effectivejava.javaparser.facade])
  (:use [clojure.test])
  (:use [effectivejava.test.helper])
  (:require [instaparse.core :as insta]))

(load "helper")

; ============================================
; Parsing
; ============================================

(deftest testParsingFileWithErrorsReturnNil
  (is (nil? (parseResource "ClassWithErrors"))))

; ============================================
; Recognize node types
; ============================================

(deftest testIsClassPositive
  (is (isClass? (parseType "ASimpleClass"))))

(deftest testIsClassNegative_Interface
  (is (not (isClass? (parseType "ASimpleInterface")))))

(deftest testIsClassNegative_Enum
  (is (not (isClass? (parseType "ASimpleEnum")))))

(deftest testIsInterfacePositive
  (is (isInterface? (parseType "ASimpleInterface"))))

(deftest testIsInterfaceNegative_Class
  (is (not (isInterface? (parseType "ASimpleClass")))))

(deftest testIsInterfaceNegative_Enum
  (is (not (isInterface? (parseType "ASimpleEnum")))))

(deftest testIsEnumPositive
  (is (isEnum? (parseType "ASimpleEnum"))))

(deftest testIsEnumNegative_Class
  (is (not (isEnum? (parseType "ASimpleClass")))))

(deftest testIsEnumNegative_Interface
  (is (not (isEnum? (parseType "ASimpleInterface")))))

; ============================================
; Modifiers
; ============================================

;(deftest testGetModifiersOnAType_NoModifiers
;  (let [t (parseType "ASimpleClass")]
;    (is (zero? (getModifiers t)))))

;(deftest testGetModifiersOnAType_TwoModifiers
;  (let [t (parseType "ASimplePublicFinalClass")]
;    (is (= 17 (getModifiers t)))))

(deftest hasPackageLevelAccess?_Positive
  (let [t (parseType "ASimpleClass")]
    (is (hasPackageLevelAccess? t))))

(deftest hasPackageLevelAccess?_Negative
  (let [t (parseType "ASimplePublicFinalClass")]
    (is (not (hasPackageLevelAccess? t)))))

; ============================================
; Naming
; ============================================

(deftest packageNameCompilationUnitEmpty
  (let [cu (parseResource "ASimpleClass")]
    (is (= "" (packageName cu)))))

(deftest packageNameCompilationUnitNotEmpty
  (let [cu (parseResource "ASimplePackage")]
    (is (= "a.simple.package_" (packageName cu)))))

(deftest packageNameNodeEmpty
  (let [t (parseType "ASimpleClass")]
    (is (= "" (packageName t)))))

(deftest packageNameNodeNotEmpty
  (let [t (parseType "ASimpleClassInAPackage")]
    (is (= "some.path" (packageName t)))))

(deftest packageNameSingleFieldDeclarationEmpty
  (let [sfd (parseTypeMember "ASimpleClass")]
    (is (= "" (packageName sfd)))))

(deftest packageNameSingleFieldDeclarationNotEmpty
  (let [sfd (parseTypeMember "ASimpleClassInAPackage")]
    (is (= "some.path" (packageName sfd)))))

