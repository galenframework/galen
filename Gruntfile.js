'use strict';

module.exports = function (grunt) {
    var banner = '/*\n<%= pkg.name %> <%= pkg.version %>';
    banner += '- <%= pkg.description %>\n<%= pkg.repository.url %>\n';
    banner += 'Built on <%= grunt.template.today("yyyy-mm-dd") %>\n*/\n';

    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);
    grunt.loadNpmTasks('grunt-mocha-test');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-plato');

    grunt.initConfig({
        galen: {
            // configurable paths
            src: 'src/main/js',
            test: 'src/test/js',
            dist: 'target'
        },
        pkg: grunt.file.readJSON('package.json'),
        jshint: {
            files: ['Gruntfile.js', 'src/*.js'],
            options: {
                maxlen: 80,
                quotmark: 'single'
            }
        },
        concat: {
            options: {
                separator: ';\n',
                banner: banner
            },
            build: {
                files: [{
                    src: ['src/*.js'],
                    dest: 'build/<%= pkg.name %>.js'
                }]
            },
        },
        uglify: {
            options: {
                banner: banner,
            },
            build: {
                files: {
                    'build/<%= pkg.name %>.min.js': ['build/<%= pkg.name %>.js'],
                }
            }
        },
        mochaTest: {
            test: {
                options: {
                    require: 'node_modules/should',
                    captureFile: '<%= galen.dist %>/surefire-reports/junitreports/TEST-mochaResults.xml',
                    reporter: 'xunit'
                },
                src: ['<%= galen.test %>/*.js']
            }
        },
        plato: {
            report: {
                files: {
                    'target/plato-report': ['<%= galen.src %>/**/*.js']
                }
            }
        }
    });

    grunt.registerTask('test', [
        'mochaTest'
    ]);

    grunt.registerTask('build', [
        'plato'
    ]);

    grunt.registerTask('default', [
        'test',
        'build'
    ]);
};