package run.strive.configcenter.exception

class ConfigCenterServiceException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)
}
