import instance from "../axios";

//login api
export function loginApi(account: string, pwd: string, captchaId: string, captcha: string) {
  return instance.post("/admin/login", {
    account,
    pwd,
    captchaId,
    captcha
  });
}

// register api
export function registerApi(account: string, pwd: string) {
  return instance.post("/admin/register", {
    account,
    pwd
  });
}

export function getCaptchaInfoApi(){
  return instance.get("/captcha");
}

export function testApi(){
    return instance.get("/func/test");
}