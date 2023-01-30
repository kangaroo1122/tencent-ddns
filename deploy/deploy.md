# 部署

配置jar包执行权限，这里将 `tencent-ddns.jar` 放在 `/opt/tencent-ddns` 目录下，按实际情况修改

`chmod +x /opt/tencent-ddns/tencent-ddns.jar`

## tencent-ddns.service

`vi /etc/systemd/system/tencent-ddns.service`

内容

```bash
[Unit]
Description=tencent-ddns
Documentation=https://www.kangaroohy.com
After=syslog.target

[Service]
User=root
ExecStart=/opt/tencent-ddns/tencent-ddns.jar start
ExecReload=/opt/tencent-ddns/tencent-ddns.jar restart
ExecStop=/opt/tencent-ddns/tencent-ddns.jar stop
SuccessExitStatus=143
Restart=always

[Install]
WantedBy=multi-user.target
```

命令如下

```bash
# 开机自启动
systemctl enable tencent-ddns

# 启动
systemctl start tencent-ddns

# 重启
systemctl restart tencent-ddns

#状态
systemctl status tencent-ddns

#关闭
systemctl stop tencent-ddns
```