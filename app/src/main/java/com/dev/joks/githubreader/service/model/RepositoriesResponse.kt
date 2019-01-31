package com.dev.joks.githubreader.service.model

class RepositoriesResponse(
    var repos: List<Repository>? = null,
    var nextPageUrl: String? = null
)