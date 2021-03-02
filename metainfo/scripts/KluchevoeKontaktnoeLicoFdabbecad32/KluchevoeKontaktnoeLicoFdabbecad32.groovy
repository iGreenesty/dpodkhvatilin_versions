if (user.metaClass.toString() != 'employee$contactPerson' || subject == null) {
  return false
}
return user && subject && user.keyEmployee && api.ou.nestedOUs(user.parent).contains(subject)